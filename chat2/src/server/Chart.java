package server;

import java.util.List;

public class Chart {
    private Integer id;
    private String user;
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Chart(String user, String content) {
        this.user = user;
        this.content = content;
    }

    public Chart() {
    }

    public Chart(String user) {
        this.user = user;
    }

    public void add(){
        DBUnitHelper.executeUpdate("INSERT INTO chat ( `user`, `content`) VALUES (?,?)",user,content);
    }

    public List<Chart> query(){
        return DBUnitHelper.executeQuery("SELECT * FROM chat where user = ?",Chart.class ,user );
    }
}
