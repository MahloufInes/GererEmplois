
package com.edt.dto;

public record EtudiantCreationResponse(
        EtudiantResponse etudiant,
        String motDePasseTemporaire
) {}
