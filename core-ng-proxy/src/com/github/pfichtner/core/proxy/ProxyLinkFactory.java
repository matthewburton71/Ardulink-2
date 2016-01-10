package com.github.pfichtner.core.proxy;

import static com.github.pfichtner.core.proxy.ProxyConnectionToRemote.Command.CONNECT_CMD;
import static org.zu.ardulink.util.Preconditions.checkNotNull;
import static org.zu.ardulink.util.Preconditions.checkState;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.pfichtner.ardulink.core.ConnectionBasedLink;
import com.github.pfichtner.ardulink.core.StreamConnection;
import com.github.pfichtner.ardulink.core.linkmanager.LinkFactory;
import com.github.pfichtner.ardulink.core.proto.api.Protocol;
import com.github.pfichtner.ardulink.core.proto.impl.ArdulinkProtocol2;

public class ProxyLinkFactory implements LinkFactory<ProxyLinkConfig> {

	public static final String OK = "OK";

	@Override
	public String getName() {
		return "proxy";
	}

	@Override
	public ConnectionBasedLink newLink(ProxyLinkConfig config)
			throws UnknownHostException, IOException {
		final ProxyConnectionToRemote remote = config.getRemote();

		remote.send(CONNECT_CMD.getCommand());
		remote.send(checkNotNull(config.getPort(), "port must not be null"));
		remote.send(String.valueOf(config.getSpeed()));
		String response = remote.read();
		checkState(OK.equals(response),
				"Did not receive ok from remote, got {}", response);
		Socket socket = remote.getSocket();
		Protocol proto = ArdulinkProtocol2.instance();
		return new ConnectionBasedLink(new StreamConnection(
				socket.getInputStream(), socket.getOutputStream(), proto),
				proto) {
			@Override
			public void close() throws IOException {
				super.close();
				remote.close();
			}
		};
	}

	public ProxyLinkConfig newLinkConfig() {
		return new ProxyLinkConfig();
	}

}
