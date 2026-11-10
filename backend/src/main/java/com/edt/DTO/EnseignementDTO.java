
package com.edt.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnseignementDTO {

    private Long id;

    private Long professeurId;
    private String professeurNom;
    private String professeurPrenom;

    private Long matiereId;
    private String matiereNom;

    private Long filiereId;
    private String filiereNom;

    private Float heuresCours;
    private Float heuresTP;
}
