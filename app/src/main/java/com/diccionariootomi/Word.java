package com.diccionariootomi;

public class Word {
    private int id;
    private String spanish;
    private String otomi;

    public Word() {
    }

    public Word(int id, String spanish, String otomi) {
        this.id = id;
        this.spanish = spanish;
        this.otomi = otomi;
    }
    public Word(String spanish, String otomi) {
        this.spanish = spanish;
        this.otomi = otomi;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpanish() {
        return spanish;
    }

    public void setSpanish(String spanish) {
        this.spanish = spanish;
    }

    public String getOtomi() {
        return otomi;
    }

    public void setOtomi(String otomi) {
        this.otomi = otomi;
    }

//    @Override
//    public String toString() {
//        return
//                "Español - " + spanish + "\n•\t" +
//                "Otomi - " + otomi;
//    }


    @Override
    public String toString() {
        return "WordM{" +
                "id=" + id +
                ", spanish='" + spanish + '\'' +
                ", otomi='" + otomi + '\'' +
                '}';
    }
}
