package br.unitins.chatbotacademia;

/**
 * Created by VMac on 17/11/16.
 */

import android.widget.ImageView;

import java.io.Serializable;

public class Message implements Serializable {
  String id, message;
  ImageView ImagemEquipamento;


  public ImageView getImagemEquipamento() {
    return ImagemEquipamento;
  }

  public void setImagemEquipamento(ImageView imagemEquipamento) {
    ImagemEquipamento = imagemEquipamento;
  }

  public Message() {
  }

  public Message(String id, String message,String UrlEquipamento, String createdAt) {
    this.id = id;
    this.message = message;


  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }




}

