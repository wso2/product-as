package org.wso2.appserver.integration.tests.protobufbinaryservice;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.*;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.client.RpcClientConnectionWatchdog;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: denuwanthi
 * Date: 6/17/14
 * Time: 9:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class StockQuoteClient {
    private static RpcClientChannel channel = null;
    private static final Log log = LogFactory.getLog(StockQuoteClient.class);
    private final String WSO2_CARBON ="wso2carbon.jks";
    private final String CLIENT_TRUSTSTORE ="client-truststore.jks";
    StockQuoteService.Void v;
    StockQuoteService.GetQuoteResponse quote;
    StockQuoteService.GetFullQuoteResponse fullQuoteResponse;
    StockQuoteService.GetMarketActivityResponse marketActivityResponse;
    String stockQuoteResponse;

    public StockQuoteService.Void getV() {
        return v;
    }

    public void setV(StockQuoteService.Void v) {
        this.v = v;
    }


    public StockQuoteService.GetQuoteResponse getQuote() {
        return quote;
    }

    public void setQuote(StockQuoteService.GetQuoteResponse quote) {
        this.quote = quote;
    }



    public StockQuoteService.GetFullQuoteResponse getFullQuoteResponse() {
        return fullQuoteResponse;
    }

    public void setFullQuoteResponse(StockQuoteService.GetFullQuoteResponse fullQuoteResponse) {
        this.fullQuoteResponse = fullQuoteResponse;
    }



    public StockQuoteService.GetMarketActivityResponse getMarketActivityResponse() {
        return marketActivityResponse;
    }

    public void setMarketActivityResponse(StockQuoteService.GetMarketActivityResponse marketActivityResponse) {
        this.marketActivityResponse = marketActivityResponse;
    }


    public String getStockQuoteResponse() {
        return stockQuoteResponse;
    }

    public void setStockQuoteResponse(String stockQuoteResponse) {
        this.stockQuoteResponse = stockQuoteResponse;
    }



    public void startClient(){
        String serverHostname = "localhost";
        int serverPort = 9001;
        String clientHostname = "localhost";
        int clientPort = 9002;
        String symbol = "IBM";

        PeerInfo client = new PeerInfo(clientHostname, clientPort);
        PeerInfo server = new PeerInfo(serverHostname, serverPort);

        try{
        DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory();
        clientFactory.setClientInfo(client);

        boolean secure = false;
        if ( secure ) {


            try {
                RpcSSLContext sslCtx = new RpcSSLContext();
                sslCtx.setKeystorePassword("wso2carbon");
                sslCtx.setKeystorePath(TestConfigurationProvider.getResourceLocation() + File.separator + "keystores" + File.separator + "products" + File.separator + WSO2_CARBON);
                sslCtx.setTruststorePassword("wso2carbon");
                sslCtx.setTruststorePath(TestConfigurationProvider.getResourceLocation() + File.separator + "keystores" + File.separator + "products" + File.separator + CLIENT_TRUSTSTORE);
                sslCtx.init();
                clientFactory.setSslContext(sslCtx);
            } catch (Exception e) {
                log.error("Couldn't create SSL Context : " + e.getLocalizedMessage());
                log.info("SSL not enanbled");
            }


        }

        ExtensionRegistry r = ExtensionRegistry.newInstance();
        StockQuoteService.registerAllExtensions(r);
        clientFactory.setExtensionRegistry(r);

        //upper bound on the peering time (to TCP settings, you can use all Netty socket options)
        clientFactory.setConnectResponseTimeoutMillis(10000);

        //If a client is also going to be acting as a server, setup an RpcCallExecutor who's
        //purpose it is to run the calls
        RpcServerCallExecutor rpcExecutor = new ThreadPoolCallExecutor(3, 10);
        clientFactory.setRpcServerCallExecutor(rpcExecutor);

        // RPC payloads are uncompressed when logged - so reduce logging
        /*CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
        logger.setLogRequestProto(false);
        logger.setLogResponseProto(false);
        clientFactory.setRpcLogger(logger);*/

        // Set up the event pipeline factory.
        // setup a RPC event listener - it just logs what happens
        RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();

        final RpcConnectionEventListener listener = new RpcConnectionEventListener() {

            @Override
            public void connectionReestablished(RpcClientChannel clientChannel) {
                log.info("connectionReestablished " + clientChannel);
                channel = clientChannel;
            }

            @Override
            public void connectionOpened(RpcClientChannel clientChannel) {
                log.info("connectionOpened " + clientChannel);
                channel = clientChannel;
            }

            @Override
            public void connectionLost(RpcClientChannel clientChannel) {
                log.info("connectionLost " + clientChannel);
            }

            @Override
            public void connectionChanged(RpcClientChannel clientChannel) {
                log.info("connectionChanged " + clientChannel);
                channel = clientChannel;
            }
        };

        rpcEventNotifier.addEventListener(listener);
        clientFactory.registerConnectionEventListener(rpcEventNotifier);

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workers = new NioEventLoopGroup(16,new RenamingThreadFactoryProxy("workers", Executors.defaultThreadFactory()));

        bootstrap.group(workers);
        bootstrap.handler(clientFactory);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);

        RpcClientConnectionWatchdog watchdog = new RpcClientConnectionWatchdog(clientFactory,bootstrap);
        rpcEventNotifier.addEventListener(watchdog);
        watchdog.start();

        CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(workers);
        shutdownHandler.addResource(rpcExecutor);

        //open a TCP connection to the server
        try {
            clientFactory.peerWith(server, bootstrap);
        } catch (IOException e) {
            log.error("TCP channel not created");
        }

        StockQuoteService.SimpleStockQuoteService.BlockingInterface blockingService = StockQuoteService.
                SimpleStockQuoteService.newBlockingStub(channel);
        final ClientRpcController controller = channel.newRpcController();
        StockQuoteService.GetQuote.Builder quoteBuilder = StockQuoteService.GetQuote.newBuilder();

        quoteBuilder.setSymbol(symbol);

        StockQuoteService.GetQuote quoterequest = quoteBuilder.build();

        try {
             quote = blockingService.getQuote(controller, quoterequest);


        } catch (ServiceException e) {
            log.warn("Call Failed");
        }
            StockQuoteService.GetFullQuote.Builder fullQuoteBuilder=StockQuoteService.GetFullQuote.newBuilder();
            fullQuoteBuilder.setSymbol(symbol);
            StockQuoteService.GetFullQuote fullQuoteRequest=fullQuoteBuilder.build();
            fullQuoteResponse=blockingService.getFullQuote(controller,fullQuoteRequest);


            StockQuoteService.GetMarketActivity.Builder marketActivityBuilder = StockQuoteService.GetMarketActivity.newBuilder();
            marketActivityBuilder.addSymbol("IBM");
            marketActivityBuilder.addSymbol("SUN");
            StockQuoteService.GetMarketActivity marketActivityRequest=marketActivityBuilder.build();
            marketActivityResponse=blockingService.getMarketActivity(controller,marketActivityRequest);

            StockQuoteService.PlaceOrder.Builder placeOrderBuilder = StockQuoteService.PlaceOrder.newBuilder();
            placeOrderBuilder.setSymbol("IBM");
            placeOrderBuilder.setPrice(100.0);
            placeOrderBuilder.setQuantity(12);
            StockQuoteService.PlaceOrder placeOrder=placeOrderBuilder.build();
            v=blockingService.placeOrder(controller,placeOrder);



        }catch (Exception e) {
                log.warn("Failure.", e);
            }

    }



}
