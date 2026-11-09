package com.erp.sistemagerencial;

import com.erp.sistemagerencial.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Teste de integração: sobe a aplicação inteira (Controller -> Service -> Repository -> banco H2)
// e faz requisições HTTP simuladas com o MockMvc. Testa o sistema como o usuário da API o vê.
@SpringBootTest
@AutoConfigureMockMvc
class SistemagerencialApplicationTests {

    @Autowired MockMvc mvc;
    @Autowired ProductRepository productRepository;

    private long createClient(String cpf) throws Exception {
        String body = mvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "Maria Silva", "cpfCnpj": "%s", "email": "maria@email.com"}
                        """.formatted(cpf)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/clients/")))
                .andReturn().getResponse().getContentAsString();
        return extractId(body);
    }

    private long createProduct(String nome, String preco, int estoque) throws Exception {
        String body = mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome": "%s", "preco": %s, "quantidadeEstoque": %d}
                        """.formatted(nome, preco, estoque)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return extractId(body);
    }

    private long extractId(String json) {
        return Long.parseLong(json.replaceAll(".*?\"id\"\\s*:\\s*(\\d+).*", "$1"));
    }

    @Test
    void fluxoCompleto_criaPedidoCalculaTotalEBaixaEstoque() throws Exception {
        long clientId = createClient("11122233344");
        long productId = createProduct("Monitor", "899.90", 10);

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clientId": %d, "items": [{"productId": %d, "quantity": 2}]}
                        """.formatted(clientId, productId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientName").value("Maria Silva"))
                .andExpect(jsonPath("$.totalAmount").value(1799.80))
                .andExpect(jsonPath("$.items[0].productName").value("Monitor"));

        assertThat(productRepository.findById(productId).orElseThrow().getQuantidadeEstoque()).isEqualTo(8);

        mvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].clientId", hasItem((int) clientId)));
    }

    @Test
    void pedidoSemEstoque_retorna422EFazRollbackDoEstoque() throws Exception {
        long clientId = createClient("55566677788");
        long comEstoque = createProduct("Cabo HDMI", "30.00", 10);
        long semEstoque = createProduct("Webcam", "250.00", 1);

        // O 1º item tem estoque, o 2º não: a transação inteira deve ser desfeita.
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clientId": %d, "items": [
                            {"productId": %d, "quantity": 5},
                            {"productId": %d, "quantity": 2}
                        ]}
                        """.formatted(clientId, comEstoque, semEstoque)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message", containsString("Estoque insuficiente")));

        assertThat(productRepository.findById(comEstoque).orElseThrow().getQuantidadeEstoque()).isEqualTo(10);
    }

    @Test
    void clienteComCpfDuplicado_retorna409() throws Exception {
        createClient("99988877766");

        mvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "Outro", "cpfCnpj": "99988877766"}
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void dadosInvalidos_retorna400ComErrosPorCampo() throws Exception {
        mvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "", "cpfCnpj": "123", "email": "nao-e-email"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.cpfCnpj").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void recursoInexistente_retorna404() throws Exception {
        mvc.perform(get("/api/clients/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrado com o ID: 999999"));
    }

    @Test
    void clienteNaoPodeDefinirOPrecoDoPedido() throws Exception {
        long clientId = createClient("12312312312");
        long productId = createProduct("Notebook", "4500.00", 3);

        // Campos extras como "unitPrice"/"totalAmount" não existem no DTO e são ignorados.
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content("""
                        {"clientId": %d, "totalAmount": 0.01,
                         "items": [{"productId": %d, "quantity": 1, "unitPrice": 0.01}]}
                        """.formatted(clientId, productId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(4500.00));
    }
}
