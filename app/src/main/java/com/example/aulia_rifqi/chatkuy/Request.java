package com.example.aulia_rifqi.chatkuy;

public class Request {

    public String name;
    public String thumb_image;
    public String status;

    public Request(){
    }


    public Request(String name,String image, String thumb_image) {
        this.name = name;
        this.thumb_image = thumb_image;
        this.status = status;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb_image() { return thumb_image; }

    public void setThumb_image(String thumb_image) { this.thumb_image = thumb_image; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
