
package com.edt.dto;

public record ProfesseurCreationResponse(
        ProfesseurResponse professeur,
        String motDePasseTemporaire
) {}
