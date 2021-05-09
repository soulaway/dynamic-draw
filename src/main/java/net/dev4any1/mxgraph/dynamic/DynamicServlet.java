package net.dev4any1.mxgraph.dynamic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Node;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

@WebServlet(urlPatterns = "/data/*", loadOnStartup = 1)
public class DynamicServlet extends HttpServlet {
	
	private static final long serialVersionUID = DynamicServlet.class.getName().hashCode();

	@Value("${dynamic.updateHost}")
	private String updateHost;

	public static int PORT = 8080;

	protected mxGraph buildGraph() {
		// Creates graph with model
		mxGraph graph = new mxGraph();
		graph.setGridEnabled(true);
		Object parent = graph.getDefaultParent();
		Object v1, v2;
		graph.getModel().beginUpdate();
		try {
			v1 = graph.insertVertex(parent, "v1", "Hello", 20, 20, 80, 30);
			v2 = graph.insertVertex(parent, "v2", "World!", 200, 150, 80, 30);
			graph.insertEdge(parent, null, "e1", v1, v2);
		}
		finally {
			graph.getModel().endUpdate();
		}
		return graph;
	}

	protected void writeResponse(HttpServletRequest request, PrintWriter writer) {
		mxCodec codec = new mxCodec();
		Node node = codec.encode(buildGraph().getModel());
		// Updates URL after initial request
		writer.println("<updates url=\""+updateHost+"/data?initialized=1\">");
		// Checks if model is initialized
		String init = request.getParameter("initialized");
		if (init == null) {
			writer.println("<model>");
			writer.println(mxXmlUtils.getXml(node));
			writer.println("</model>");
			writer.println("<fit max-scale=\"2\"/>");
		}
		else {
			String c1 = (Math.random() < 0.5) ? "red" : ((Math.random() < 0.5) ? "green" : "blue");
			String c2 = (Math.random() < 0.5) ? "red" : ((Math.random() < 0.5) ? "green" : "blue");		
			// Updates the color (other possible updates include label,
			// metadata, tooltip, geometry)
			writer.println(
					"<update id=\"v1\" style=\"fillColor=" + c1 + ";\"></update>");
			writer.println(
					"<update id=\"v2\" style=\"fillColor=" + c2 + ";\"></update>");
		}
		writer.println("</updates>");
		System.out.println("update t " + System.currentTimeMillis());		
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.setContentType("text/xml; charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String encoding = request.getHeader("Accept-Encoding");
		// Supports GZIP content encoding
		if (encoding != null && encoding.indexOf("gzip") >= 0) {
			response.setHeader("Content-Encoding", "gzip");
			out = new GZIPOutputStream(out);
		}
		PrintWriter writer = new PrintWriter(out);
		writeResponse(request, writer);
		writer.flush();
		writer.close();
	}
}
