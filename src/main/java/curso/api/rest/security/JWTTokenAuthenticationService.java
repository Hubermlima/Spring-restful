package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.UserSystem;
import curso.api.rest.repository.IUserSystem;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAuthenticationService {

	// Tempo de validade do Token = 2 dias (em milisegundos)
	private static final long EXPIRATION_TIME = 172800000;
	// Senha unica para compor a autenticacao e ajudar na seguranca
	private static final String SECRET = "SenhaExtremamenteSecreta";
	// Prefixo padrao de token
	private static final String TOKEN_PREFIX = "Bearer";
	private static final String HEADER_STRING = "Authorization";

	// Gerando token de autenticacao e adicionando ao cabeçalho e resposta HTTP
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		// Montagem do Token
		String JWT = Jwts.builder() // Chama o gerador de token
				.setSubject(username) // Adiciona o usuario
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); // Compactação e algoritmo de geracao de senha

		// Junta token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT;

		// Adiciona no cabeçalho http
		response.addHeader(HEADER_STRING, token); // Authorization: Bearer 398593lmgla98t9eqo5l5k4l

		corsLiberation(response);
		
		// Escreve token como responsta no corpo http
		response.getWriter().write("{\"Authorizaton\": \"" + token + "\"}");

	}

	// Retorna o usuario validado com token ou caso não seja válido retorna null
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		// Pega o token enviado no cabecalho http
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {
			// Faz a validacao do token do usuario na requisicao
			String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
					.getSubject();

			if (user != null) {

				UserSystem userSystem = ApplicationContextLoad.getApplicationContext().getBean(IUserSystem.class)
						.findUserByLogin(user);

				if (userSystem != null) {
					return new UsernamePasswordAuthenticationToken(userSystem.getUsername(), userSystem.getPassword(),
							userSystem.getAuthorities());
				}
			}
		}

		// Liberando resposta para porta diferente do projeto Angular
		corsLiberation(response);
		return null; // Nao autorizado
	}

	private void corsLiberation(HttpServletResponse response) {
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
		     response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
		     response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {
		     response.addHeader("Access-Control-Request-Headers", "*");
		}
		
	}
}
