package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Override  // Configura as solicitações de acesso por HTTP
	protected void configure(HttpSecurity http) throws Exception {
		
		// Ativando a protecao contra usuarios que não estao validados por token
		http.csrf()
		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		//Ativando permissao de acesso a pagina inicial do sistema
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		
		// Redireciona apos usuario se deslogar do sistema
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		// Mapeia a URL de logout e invalida o usuario
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        // Filtra requisicoes de login para autenticacao
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
        // Filtra demais requisicoes para verificar a presenca do token jwt no header http
		.addFilterBefore(new JWTApiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	@Override // Cria autenticação do usuario com banco de dados  ou em memoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(new BCryptPasswordEncoder());
		
		
	}

	@Override // Ignora URL especificas
	public void configure(WebSecurity web) throws Exception {
         web.ignoring().antMatchers("/materialize/**");
	}
}
