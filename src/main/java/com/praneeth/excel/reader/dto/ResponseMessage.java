package com.praneeth.excel.reader.dto;

import java.util.List;

public class ResponseMessage {
  private String message;
  private List<User> users;

  public ResponseMessage(String message) {
    this.message = message;
  }

  public ResponseMessage(String message, List<User> users) {
    this.message = message;
    this.users = users;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }
}