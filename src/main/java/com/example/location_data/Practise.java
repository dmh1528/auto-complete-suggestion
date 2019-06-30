package com.example.location_data;

import javax.print.attribute.standard.Fidelity;
import java.io.File;

public class Practise {

    private static String igPath300="";

    public static void main(String args[]) {
        System.out.println(DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_300);
        DocvecComputeVersion d = DocvecComputeVersion.DOCVEC_COMPUTE_VERSION_150;
        System.out.println(d.getDocvecComputeVersion());
/*
        File fileIg300Path = new File("/home/himanshuk/Downloads/igPath300");

        for(String ig300Files:fileIg300Path.list()){
            String lang = ig300Files.split("\\.")[0];
            System.out.println(lang);
        }*/

    }

}
