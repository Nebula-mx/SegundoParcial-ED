const solveUrl = '/solve';
const $resultDiv = document.getElementById('result');

const sampleRequestData = {
  expr: `y'' - 2y' + y = arctan(x)`,
  method: "AUTO",
  conditions: ["y(0) = 1", "y'(0) = 0"]
}

export function showRequestData(requestData) {
  let res = fetch(solveUrl, {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
      console.log(data);
      $resultDiv.innerHTML = '';
      $resultDiv.innerHTML = `
          <h3>Resultado final: </h3>
          <p class="finalResText">${data.finalSolution}</p>
          <h4>Pasos intermedios:</h4>
      `;

      data.steps.forEach(step => {
        $resultDiv.insertAdjacentHTML("beforeend", `<li><legend class="stepTitle"> ${step.title} </legend>`);
        $resultDiv.insertAdjacentHTML("beforeend", `<p class="stepDesc"> ${step.explanation} </p>`);
        step.expressions.forEach(expr => {
          $resultDiv.insertAdjacentHTML("beforeend", `<p class="stepExpr"> ${expr} </p></li>`);
        });
      })

      if(data.hasOwnProperty('initialConditionsList')){
        $resultDiv.insertAdjacentHTML("beforeend", `<li><legend class="stepTitle">✏️ Condiciones Iniciales:</legend>`);
        data.initialConditionsList.forEach(cond => {
          $resultDiv.insertAdjacentHTML("beforeend", `<p class="stepExpr"> ${cond} </p>`);
        });
        $resultDiv.insertAdjacentHTML("beforeend", `<p class="finalResText">${data.conditionsSolution}</p>`);
      }

      $resultDiv.insertAdjacentHTML("beforeend", `</ol>`);
    })
    .catch(error => {
        console.error('Error:', error);
    })
}