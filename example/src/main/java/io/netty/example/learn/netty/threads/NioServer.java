package io.netty.example.learn.netty.threads;

/**
 * Created by pengfei.dong
 * Date 2018/7/23
 * Time 下午7:55
 */


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new Thread(new Acceptor()).start();

    }

    /**
     * 连接线程模型，反应堆，转发器 Acceptor
     *
     * @author dingwei2
     *
     */
    private static final class Acceptor implements Runnable {

        private NioReactorThreadGroup nioReactorThreadGroup;

        public Acceptor() {
            nioReactorThreadGroup = new NioReactorThreadGroup();
        }

        public void run() {
            // TODO Auto-generated method stub
            System.out.println("服务端启动成功，等待客户端接入");
            ServerSocketChannel ssc = null;
            Selector selector = null;
            try {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress("127.0.0.1", 9080));

                selector = Selector.open();
                ssc.register(selector, SelectionKey.OP_ACCEPT);

                Set<SelectionKey> ops = null;
                while (true) {
                    try {
                        selector.select(); // 如果没有感兴趣的事件到达，阻塞等待
                        ops = selector.selectedKeys();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        break;
                    }

                    // 处理相关事件
                    for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                        SelectionKey key = it.next();
                        it.remove();

                        try {
                            if (key.isAcceptable()) { // 客户端建立连接
                                System.out.println("收到客户端的连接请求。。。");
                                ServerSocketChannel serverSc = (ServerSocketChannel) key.channel();// 这里其实，可以直接使用ssl这个变量
                                SocketChannel clientChannel = serverSc.accept();
                                clientChannel.configureBlocking(false);
                                nioReactorThreadGroup.dispatch(clientChannel); // 转发该请求
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            System.out.println("客户端主动断开连接。。。。。。。");
                        }

                    }
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}
