package FileUtils;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.hadoop.fs.ftp.FTPException;

import java.util.ArrayList;
import java.util.List;

public class FTPServer {
    private FtpServerFactory serverFactory = new FtpServerFactory();
    private ListenerFactory listenerFactory= new ListenerFactory();

    public FTPServer() {
        listenerFactory.setPort(21);
        serverFactory.addListener("default", listenerFactory.createListener());
        addUserAdmin();
    }

    public FTPServer(int port) {
        listenerFactory.setPort(port);
        serverFactory.addListener("default", listenerFactory.createListener());
        addUserAdmin();

    }
    public void run() {
        FtpServer ftpServer= serverFactory.createServer();
        try{
            if (ftpServer.isStopped())
        ftpServer.start();
        } catch (FtpException err){
            err.printStackTrace();
        }
    }
    public void stop(){
        try{
            if(!serverFactory.createServer().isStopped())
                serverFactory.createServer().stop();
        }catch (FTPException err){
            err.printStackTrace();
        }
    }
    private void addUserAdmin() {
        String userName = "admin";
        String password = "acer5553gati";
        BaseUser user = new BaseUser();
        user.setName(userName);
        user.setPassword(password);
        user.setHomeDirectory("/tmp/"+userName);

        List<Authority> authorityList = new ArrayList<Authority>();
        authorityList.add(new WritePermission());
        user.setAuthorities(authorityList);
        try {
            serverFactory.getUserManager().save(user);
        }catch (FtpException err)
        {
            err.printStackTrace();
        }
    }
    public void addUser(String userName,String password) throws FtpException{
        BaseUser user = new BaseUser();
        user.setName(userName);
        user.setPassword(password);
        user.setHomeDirectory("/tmp/"+userName);

        List<Authority> authorityList = new ArrayList<Authority>();
        authorityList.add(new WritePermission());
        user.setAuthorities(authorityList);
        serverFactory.getUserManager().save(user);

    }
}
