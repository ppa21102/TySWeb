package edu.uclm.esi.tysweb2023.ws;
import org.springframework.web.socket.WebSocketSession;

import jakarta.websocket.Extension;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class FakeWSBot implements WebSocketSession {

    public void sendMessage(TextMessage message) throws IOException {
        System.out.println("Mensaje enviado a la sesión fake: " + message.getPayload());
    }

    @Override
    public String getId() {
        return "fakeSessionId";
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {}

    @Override
    public void close(CloseStatus status) throws IOException {}

    @Override
    public URI getUri() {
        return URI.create("ws://fakeSession");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return null;
    }

    @Override
    public String getAcceptedProtocol() {
        return null;
    }

    public void setAcceptedProtocol(String protocol) {}

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress("localhost", 8080);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress("localhost", 8080);
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {}

    @Override
    public int getBinaryMessageSizeLimit() {
        return 1024 * 1024;
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {}

    @Override
    public int getTextMessageSizeLimit() {
        return 1024 * 1024;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return null;
    }

    public boolean isSecure() {
        return false;
    }

    public void start() {}

    public void stop() {}

    public boolean isStarted() {
        return true;
    }

    public boolean isActive() {
        return true;
    }

    public void ping() {}

    public void pong() {}

    public void shutdown() {}

    public boolean isShutdown() {
        return false;
    }

    public void setTimeout(long timeout) {}

    public long getTimeout() {
        return 0;
    }

    public void setBufferSize(int bufferSize) {}

    public int getBufferSize() {
        return 1024 * 1024;
    }

    public ByteBuffer allocateBuffer(int size) {
        return ByteBuffer.allocate(size);
    }

    public void closeSocket() throws IOException {}

	@Override
	public Principal getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(WebSocketMessage<?> message) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
