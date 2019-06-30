package com.example.location_data;

public enum DocvecComputeVersion {
    DOCVEC_COMPUTE_VERSION_150("1_X_150"),DOCVEC_COMPUTE_VERSION_300("1_X_300");

    private String docVecComputeVersion;
    private String docVecCompute;

    private DocvecComputeVersion(String docVecComputeVersion) {
        this.docVecComputeVersion = docVecComputeVersion;
    }

    public String getDocvecComputeVersion() {
        return this.docVecComputeVersion;
    }

}
