package ind.web.nhp.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserBEFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UserBEForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required.email",
		        "Email is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "loginName", "required.loginName",
		        "Login Name is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password",
		        "Password is required.");
	}

}
