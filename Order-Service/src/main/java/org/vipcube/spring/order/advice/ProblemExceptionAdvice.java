package org.vipcube.spring.order.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;

@ControllerAdvice
public class ProblemExceptionAdvice implements ProblemHandling, ValidationAdviceTrait, GeneralAdviceTrait {
}
