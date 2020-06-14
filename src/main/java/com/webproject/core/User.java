package com.webproject.core;

import java.security.Principal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webproject.utils.Utils;

@Entity
@Table(name = "users",
              indexes = {@Index(name = "usernameIndex",  columnList="username", unique = true)})
@NamedQueries({
        @NamedQuery(
                name = "com.webproject.core.User.findAll",
                query = "SELECT u FROM User u"
        )
})
public class User implements Principal {
	@Id
    @Column(name = "username", nullable = false)
	@JsonProperty("username")
    private String username;
    
	@JsonProperty("fullname")
    @Column(name = "fullname", nullable = false)
    private String fullName;

	@JsonProperty("email")
    @Column(name = "email", nullable = false)
    private String email;

	@JsonProperty("role")
    @Column(name = "role", nullable = false)
    private String role;

	@JsonProperty("md5pass")
    @Column(name = "md5pass", nullable = false)
    private String md5pass;

    public User() {
    }
    
    public User(String uname){
    	this.username = uname;
    }
    
    public User(String uname, String pass, String fName, String email, String role) {
        this.username = uname;
        this.md5pass = Utils.getMD5(pass);
        this.fullName = fName;
        this.email = email;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    
    @JsonIgnore
    public String getMd5Password(){
    	return md5pass;
    }
    
    @JsonIgnore
    public String getName() {
        return username;
    }
    
    @JsonIgnore
    public String getId(){
    	return username;
    }
    

    public String getRole() {
        return role;
    }

    
    @JsonProperty
    @JsonIgnore
    public String[] getPermissions() {
    	if(role.equals("ADMIN")){
    		return new String[]{"controller"};
    	}else{
    		return new String[]{""};
    	}
    }

	public String getMD5Password() {
		return md5pass;
	}

	public void setMD5Password(String md5Password) {
		md5pass = md5Password;
	}

	public void update(User updates) {
		md5pass = updates.md5pass;
		email = updates.email;
		role = updates.role;
		fullName = updates.fullName;
	}

}
