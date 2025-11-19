function validateInput(element) {
    const value = element.value.trim();
    const isNumber = /^-?\d*\.?\d+$/.test(value);
    const isVariable = /^y(\(\d+\))?(\(\d+\))*$/.test(value);
    
    if (isNumber || isVariable) {
        element.style.border = "2px solid green";
        element.classList.remove("shake-error");
        return true;
    } else {
        element.style.border = "2px solid red";
        element.classList.add("shake-error");
        setTimeout(() => {
            element.classList.remove("shake-error");
        }, 500);
        return false;
    }
}

export function validateAllInputs() {
    const inputs = document.querySelectorAll('#initialConditionsForm input[type="text"]');
    let allValid = true;
    
    inputs.forEach(input => {
        if (!validateInput(input)) {
            allValid = false;
        }
    });
    
    return allValid;
}