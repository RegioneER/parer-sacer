/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author DiLorenzo_F
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FiltroMultiploUnitaDocValidator.class)
@Documented
public @interface ValidateFiltroMultiploUnitaDoc {
    String message() default "Filtro multiplo su criterio unità doc non valido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
