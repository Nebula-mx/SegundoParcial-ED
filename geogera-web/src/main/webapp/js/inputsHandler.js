import { isDifferentialEquation } from "./validateEquation.js";
import { validateAllInputs } from "./validateCondition.js";
import { showRequestData } from "./showRequestData.js";


const $equationInp = document.getElementById("equation");
const $initialConditionsForm = document.getElementById("initialConditionsForm");
const $addConditionBtn = document.getElementById("addInitialCondition");
const $solveEqBtn = document.getElementById("resolveEquation");
const $resMethodForm = document.getElementById("resMethod-select");

const clickableItems = {
  addInitialCondition: (e) => {
    console.log(e.target);
    
    $initialConditionsForm.insertAdjacentHTML("beforeend", `
      <div class="formDoubleInputsContainer">
          <input type="text" name="conditionVariable" class="IC-variableValues" name="initialConditions" placeholder="y(0)" required value="y(0)">
          <input type="text" name="conditionValue" class="IC-variableResult" name="initialConditions" placeholder="Valor" required>
      </div>  
    `)

    if ($initialConditionsForm.children.length >= 2 && !document.getElementById("removeInitialCondition")) {
      $initialConditionsForm.parentElement.insertAdjacentHTML("beforeend", `
        <button type="button" id="removeInitialCondition" class="formBtn secondaryBtn" style="margin-top: clamp(8px, 2vw, 16px);">Remover condición inicial</button>  
      `);
    }
  },
  removeInitialCondition: (e) => {
    console.log(e.target);
    
    const conditionInputs = $initialConditionsForm.getElementsByClassName("formDoubleInputsContainer");
    if (conditionInputs.length > 1) {
      $initialConditionsForm.removeChild(conditionInputs[conditionInputs.length - 1]);
    }

    if (conditionInputs.length === 1) {
      e.target.remove();
    }
  },
  resolveEquation: (e) => {
    let conditionsForm = new FormData($initialConditionsForm);
    let conditionsVariables = conditionsForm.getAll("conditionVariable");
    let conditionsValues = conditionsForm.getAll("conditionValue");

    let methodsForm = new FormData($resMethodForm);

    console.log(conditionsForm.getAll("conditionVariable"));
    console.log(conditionsForm.getAll("conditionValue"));

    const requestData = {
      expr: "",
      method: "AUTO",
      conditions: []
    };

    if (isDifferentialEquation($equationInp.value)) {
      requestData.expr = $equationInp.value;
    } else {
      alert("La ecuación ingresada no es una ecuación diferencial válida.");
      return;
    }

    //si es false, hay condiciones iniciales
    let hasConditions = conditionsValues.length > 0 && conditionsValues[0].trim() !== "";
    console.log("existingConditions: ", hasConditions);

    if (hasConditions) {
      let res = validateAllInputs();

      if (!res) {
        alert("Hay condiciones iniciales inválidas. Por favor, revíselas.");
        return;
      }

      for (let i = 0; i < conditionsVariables.length; i++) {
        if (conditionsVariables[i].trim() !== "" && conditionsValues[i].trim() !== "") {
          let condStr = `${conditionsVariables[i]} = ${conditionsValues[i]}`;
          requestData.conditions.push(condStr);
        }
      }
    }

    const selectedMethod = methodsForm.get("method");
    requestData.method = selectedMethod;

    console.log(requestData);

    showRequestData(requestData);
  }
}

function clickHandler(e) {
  console.log("Clicked: ", e.target.id);
  if (clickableItems.hasOwnProperty(e.target.id)) {
    clickableItems[e.target.id](e)
  }
}

export function initEventHandlers() {
  document.addEventListener("click", clickHandler)
}