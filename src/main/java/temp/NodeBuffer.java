package temp;

import java.util.HashMap;
import java.util.Map;

import com.efsol.tagml.lex.CloseToken;
import com.efsol.tagml.lex.OpenToken;

public class NodeBuffer {
	private StringBuilder buffer;
	private Map<String, Bead> context;
	private Position position;
	private ArrayList

	public NodeBuffer() {
		this.buffer = new StringBuilder();
		this.context = new HashMap<>();
		this.position = null;
	}

	public void openTag(OpenToken tag) {

	}

	public void closeTag(CloseToken tag) {

	}

	public void joinLayer(String name) {
		// TODO
	}

	public void leaveLayer(String name) {
		// TODO
	}

	public void append(String text) {
		this.buffer.append(text);
	}

	public Node swap() {
		System.out.println("NodeBuffer.swap pos=" + position);
		Node ret = null;
		if (buffer.length() > 0) {
			String text = buffer.toString();
			buffer.setLength(0);
			ret = new Node(text, context, position);
		}
		this.position = null;
		return ret;
	}

	public boolean isIncomplete() {
		return buffer.length() > 0;
	}

	public void setPosition(Position position) {
		System.out.println("setPosition old=" + this.position + " new=" + position);
		if (null == this.position) {
			this.position = position;
		}
	}
}
