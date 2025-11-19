export function isDifferentialEquation(expression) {
    const derivativePatterns = [
        /y''''/g,
        /y'''/g,
        /y''/g,
        /y'/g,
        /d²y\/dx²/g,
        /dy\/dx/g,
        /y\((\d+)\)/g,
        /\\frac{d\^{.*}y}{dx\^{.*}}/g,
        /\\frac{d.*y}{dx.*}/g
    ];

    const cleanedExpression = expression.replace(/\s/g, '');
    
    for (const pattern of derivativePatterns) {
        const matches = cleanedExpression.match(pattern);
        if (matches && matches.length > 0) {
            return true;
        }
    }
    
    return false;
}