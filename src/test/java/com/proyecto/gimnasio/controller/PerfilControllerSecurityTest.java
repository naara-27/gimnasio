package com.proyecto.gimnasio.controller;

import com.proyecto.gimnasio.model.Usuario;
import com.proyecto.gimnasio.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PerfilControllerSecurityTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UsuarioService service;

        @Test
        @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
        void debePermitirActualizarPropioPerfil() throws Exception {
                Usuario clientePropio = new Usuario();
                clientePropio.setId(1L);
                clientePropio.setNombre("Juan");
                clientePropio.setCedula("11111111");
                clientePropio.setEmail("cliente@test.com");
                clientePropio.setTelefono("12345678");
                clientePropio.setRol("CLIENTE");
                clientePropio.setActivo(true);

                when(service.buscarPorEmail("cliente@test.com")).thenReturn(clientePropio);
                when(service.actualizarPerfilCliente(any(Usuario.class), any(String.class)))
                                .thenAnswer(inv -> inv.getArgument(0));

                mockMvc.perform(post("/usuarios/perfil/actualizar")
                                .param("id", "1")
                                .param("nombre", "Juan")
                                .param("cedula", "11111111")
                                .param("email", "cliente@test.com")
                                .param("telefono", "12345678")
                                .param("rol", "CLIENTE")
                                .param("activo", "true")
                                .param("nuevaPassword", "")
                                .param("confirmPassword", ""))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/usuarios/perfil"));
        }

        @Test
        @WithMockUser(username = "cliente1@test.com", roles = "CLIENTE")
        void debeRechazarActualizarPerfilDeOtroCliente() throws Exception {
                Usuario clientePropio = new Usuario();
                clientePropio.setId(1L);
                clientePropio.setNombre("Cliente 1");
                clientePropio.setCedula("11111111");
                clientePropio.setEmail("cliente1@test.com");
                clientePropio.setTelefono("12345678");
                clientePropio.setRol("CLIENTE");
                clientePropio.setActivo(true);

                when(service.buscarPorEmail("cliente1@test.com")).thenReturn(clientePropio);

                mockMvc.perform(post("/usuarios/perfil/actualizar")
                                .param("id", "2")
                                .param("nombre", "Cliente 2 Modificado")
                                .param("cedula", "22222222")
                                .param("email", "cliente2@test.com")
                                .param("telefono", "87654321")
                                .param("rol", "CLIENTE")
                                .param("activo", "true"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/perfil"))
                                .andExpect(model().attributeHasErrors("cliente"));
        }

        @Test
        @WithMockUser(username = "admin@test.com", roles = "ADMIN")
        void adminPuedeActualizarCualquierPerfil() throws Exception {
                Usuario clienteAdmin = new Usuario();
                clienteAdmin.setId(99L);
                clienteAdmin.setNombre("Admin");
                clienteAdmin.setCedula("99999999");
                clienteAdmin.setEmail("admin@test.com");
                clienteAdmin.setTelefono("11111111");
                clienteAdmin.setRol("ADMIN");
                clienteAdmin.setActivo(true);

                when(service.buscarPorEmail("admin@test.com")).thenReturn(clienteAdmin);
                when(service.actualizarPerfilCliente(any(Usuario.class), any(String.class)))
                                .thenAnswer(inv -> inv.getArgument(0));

                mockMvc.perform(post("/usuarios/perfil/actualizar")
                                .param("id", "2")
                                .param("nombre", "Cliente 2 Modificado")
                                .param("cedula", "22222222")
                                .param("email", "cliente2@test.com")
                                .param("telefono", "87654321")
                                .param("rol", "CLIENTE")
                                .param("activo", "true"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/usuarios/perfil"));
        }
}
