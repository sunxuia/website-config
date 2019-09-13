package net.sunxu.website.config.security.authentication;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sunxu.website.test.helputil.authtoken.AuthTokenBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getPrincipalWoAuthorization() throws Exception {
        mockMvc.perform(get("/principal"))
                .andExpect(status().is(200))
                .andExpect(content().string(""));
    }

    @Test
    public void getPrincipalWithAuthoriztion() throws Exception {
        String token = new AuthTokenBuilder().name("admin").addRole("ROLE_TEST").build();
        String res = mockMvc.perform(get("/principal")
                .header(AuthTokenDefine.TOKEN_HEADER_NAME, AuthTokenDefine.TOKEN_PREFIX + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var principal = objectMapper.readValue(res, UserPrincipal.class);
        Assert.assertEquals(Long.valueOf(100), principal.getId());
        Assert.assertEquals("admin", principal.getUserName());
        Assert.assertArrayEquals(new String[]{"ROLE_TEST"}, principal.getRoles().toArray(new String[1]));
    }

    @Test
    public void expiredTokenNotAllowed() throws Exception {
        String token = new AuthTokenBuilder().name("admin").addRole("ROLE_TEST").exipreSeconds(-1L).build();
        mockMvc.perform(get("/principal")
                .header(AuthTokenDefine.TOKEN_HEADER_NAME, AuthTokenDefine.TOKEN_PREFIX + token))
                .andExpect(status().is(200))
                .andExpect(content().string(""));
    }
}
