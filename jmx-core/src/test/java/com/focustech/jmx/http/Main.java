package com.focustech.jmx.http;


public class Main {
    // public static void main(String[] args) {
    // Socket socket = null;
    // BufferedReader bufferedreader = null;
    // PrintWriter pw = null;
    // try {
    // System.out.println("server starting..");
    // startMonitor();
    // ServerSocket serverSocket = new ServerSocket(8888);
    // System.out.println("server started..");
    // while (true) {
    // socket = serverSocket.accept();
    // System.out.println("客户端请求：" + socket.getRemoteSocketAddress());
    // InputStream inputStream = socket.getInputStream();
    // bufferedreader = new BufferedReader(new InputStreamReader(inputStream));
    // String info = bufferedreader.readLine();
    // System.out.println(info);
    // OutputStream op = socket.getOutputStream();
    //
    // if (org.apache.commons.lang.StringUtils.equals("dump", info)) {
    // File f = new File("thread-dump_demo_2014-12-29-18-12-26.txt");
    // long l = f.length();
    // int length = 0;
    // double sumL = 0;
    // DataOutputStream dos = new DataOutputStream(op);
    // FileInputStream fis = new FileInputStream(f);
    // byte[] bytes = new byte[1024];
    // while ((length = fis.read(bytes)) > -1) {
    // sumL += bytes.length;
    // System.out.println("已经传出" + ((sumL / l) * 100) + "%");
    //
    // dos.write(bytes, 0, length);
    // dos.flush();
    // }
    // System.out.println("文件发送...");
    // dos.close();
    // }
    // else {
    // List<MBean> list = new ArrayList<MBean>();
    // list.add(responseVal("resin:type=ConnectionPool,name=jdbc/mlanDatabase"));
    // list.add(responseVal("resin:type=ThreadPool"));
    // ObjectOutputStream oos = new ObjectOutputStream(op);
    // oos.writeObject(list);
    // oos.close();
    // }
    //
    // }
    // }
    // catch (IOException e) {
    // e.printStackTrace();
    // }
    // finally {
    // try {
    // bufferedreader.close();
    // socket.close();
    // }
    // catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // }

    public static void main(String[] args) {
        startMonitor();
    }

    private static void startMonitor() {
        // Server server =
        // Server.builder()
        // .setHost("192.168.10.25")
        // .setPort("1099")
        // .setAlias("demo")
        // .setNumQueryThreads(1)
        // // .setCronExpression("0/10 00 0 * * ?")
        // .addQueries(
        // Query.builder()
        // .setObj("resin:type=ConnectionPool,name=jdbc/mlanDatabase")
        // .addOutputWriter(
        // new InterfaceOutputWriter(ImmutableList.<String> of(), Collections
        // .<String, Object> emptyMap())).build(),
        // Query.builder()
        // .setObj("resin:type=ThreadPool")
        // .addOutputWriter(
        // new InterfaceOutputWriter(ImmutableList.<String> of(), Collections
        // .<String, Object> emptyMap())).build()).build();
        //
        // JmxProcess process = new JmxProcess(server);
        // JmxWatcherConfiguration jmxWatcherConfig = new JmxWatcherConfiguration();
        // jmxWatcherConfig.setRunPeriod(10);
        // Injector injector = Guice.createInjector(new (jmxWatcherConfig));
        // JmxWatcher jmxMonitor = injector.getInstance(JmxWatcher.class);
        //
        // try {
        // jmxMonitor.executeStandalone(process);
        // }
        // catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    // private static MBean responseVal(String server) {
    // List<Result> list = ResultManager.getServerVal(server);
    // MBean bean = new MBean();
    // bean.setBeanName(server);
    // bean.setProperties(list);
    // return bean;
    // }
}
