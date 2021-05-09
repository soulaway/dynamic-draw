package net.dev4any1.mxgraph.dynamic;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

/**
 * Handing 
 * draw.io core host
 * update-interval
 * update host
 * might be adjusted with additional params for passing onto update method
 * @author user
 *
 */

@WebServlet(urlPatterns = "/draw/*", loadOnStartup = 1)
public class DrawServlet extends HttpServlet{

	private static final long serialVersionUID = DrawServlet.class.getName().hashCode();
	@Value("${dynamic.drawHost}")
	private String drawHost;
	@Value("${dynamic.updateHost}")
	private String updateHost;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String toUrl = buildUrl(drawHost, updateHost, 200);
		System.out.println(toUrl);
		response.sendRedirect(toUrl);
	}

	protected String buildUrl(String drawHost, String updateHost, int updateIntervalMilis) {
		return String.format("%s/?lightbox=1&test=1&p=update&https=%s&update-interval=%d&update-url=%s/data",
				drawHost,
				drawHost.startsWith("https")? 1 : 0,
				updateIntervalMilis,
				updateHost);
	}
}
