function createExpressionInput(value = "") {
  return `
    <div class="input-group">
      <label class="label">Expression</label>
      <input type="text" class="expression-input border p-2 rounded w-full" placeholder="Enter expression" value="${value}" />
    </div>`;
}

function addSimpleCondition(parent = document.getElementById("conditionSection"), defaultType = "SPEL", expression = "") {
  const container = document.createElement("div");
  container.className = "border p-3 rounded bg-gray-50 space-y-2";

  container.innerHTML = `
    <div class="flex justify-between items-center">
      <span class="font-medium">Simple Condition</span>
      <button onclick="this.closest('div.border').remove(); updateJsonPreview();" class="text-red-500 text-sm">Remove</button>
    </div>
    <select class="condition-type border p-2 rounded w-full">
      ${["SPEL", "JEXL", "OBJECT_FIELD_MATCH", "ALWAYS_TRUE"]
  .map(t => `<option value="${t}" ${t === defaultType ? "selected" : ""}>${t}</option>`)
  .join("")}
    </select>
    ${createExpressionInput(expression)}
  `;

  parent.appendChild(container);
  updateJsonPreview();
}

function addCompositeCondition() {
  const parent = document.getElementById("conditionSection");
  const wrapper = document.createElement("div");
  wrapper.className = "border p-3 rounded bg-blue-50 space-y-2";

  wrapper.innerHTML = `
    <div class="flex justify-between items-center">
      <span class="font-medium">Composite Condition</span>
      <button onclick="this.closest('div.border').remove(); updateJsonPreview();" class="text-red-500 text-sm">Remove</button>
    </div>
    <div>
      <label class="label">Operator</label>
      <select class="composite-operator border p-2 rounded w-full">
        <option value="AND">AND</option>
        <option value="OR">OR</option>
      </select>
    </div>
    <div class="composite-conditions space-y-3"></div>
    <button type="button" onclick="addSimpleCondition(this.parentNode.querySelector('.composite-conditions'))" class="px-3 py-1 bg-blue-600 text-white rounded">+ Add Sub-Condition</button>
  `;

  parent.appendChild(wrapper);
  updateJsonPreview();
}

function addConditionBlock() {
  const type = document.getElementById("conditionType").value;
  if (type === "simple") {
    addSimpleCondition();
  } else {
    addCompositeCondition();
  }
}

function addAction(defaultType = "SPEL", expression = "") {
  const container = document.getElementById("actions");
  const div = document.createElement("div");
  div.className = "border p-3 rounded bg-gray-50 space-y-2";

  div.innerHTML = `
    <div class="flex justify-between items-center">
      <span class="font-medium">Action</span>
      <button onclick="this.closest('div.border').remove(); updateJsonPreview();" class="text-red-500 text-sm">Remove</button>
    </div>
    <select class="action-type border p-2 rounded w-full">
      ${["SPEL", "JEXL"].map(t => `<option ${t === defaultType ? "selected" : ""}>${t}</option>`).join("")}
    </select>
    ${createExpressionInput(expression)}
  `;
  container.appendChild(div);
  updateJsonPreview();
}

function collectConditions() {
  const sections = document.querySelectorAll("#conditionSection > .border");
  if (sections.length === 0) return null;

  // if only one and it's simple, skip composite
  if (sections.length === 1 && !sections[0].querySelector(".composite-operator")) {
    const type = sections[0].querySelector(".condition-type").value;
    const expression = sections[0].querySelector(".expression-input").value;
    return {
      name: null,
      operator: null,
      conditions: null,
      type,
      parameters: { expression }
    };
  }

  // Composite
  const composite = {
    name: null,
    operator: "AND",
    conditions: [],
    type: null,
    parameters: null
  };

  sections.forEach(section => {
    const opSelect = section.querySelector(".composite-operator");
    if (opSelect) {
      const subConditions = section.querySelectorAll(".composite-conditions > .border");
      const nested = [];
      subConditions.forEach(sc => {
        const type = sc.querySelector(".condition-type").value;
        const expression = sc.querySelector(".expression-input").value;
        nested.push({
          name: null,
          operator: null,
          conditions: null,
          type,
          parameters: { expression }
        });
      });
      composite.operator = opSelect.value;
      composite.conditions.push(...nested);
    } else {
      const type = section.querySelector(".condition-type").value;
      const expression = section.querySelector(".expression-input").value;
      composite.conditions.push({
        name: null,
        operator: null,
        conditions: null,
        type,
        parameters: { expression }
      });
    }
  });

  return composite;
}

function collectActions() {
  return Array.from(document.querySelectorAll("#actions > .border")).map(div => {
    const type = div.querySelector(".action-type").value;
    const expression = div.querySelector(".expression-input").value;
    return {
      type,
      parameters: { expression }
    };
  });
}

function collectFormData() {
  return {
    updateHistory: [],
    name: document.getElementById("name").value,
    _id: document.getElementById("id").value,
    priority: parseInt(document.getElementById("priority").value),
    bucket: document.getElementById("bucket").value,
    description: document.getElementById("description").value,
    enabled: document.getElementById("enabled").checked,
    condition: collectConditions(),
    actions: collectActions()
  };
}

function updateJsonPreview() {
  const data = collectFormData();
  document.getElementById("jsonPreview").textContent = JSON.stringify(data, null, 2);
}

function submitRule() {
  const data = collectFormData();
  fetch(`http://localhost:8383/v1/cdn/rule/${data._id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "x-auth-bypass-token": "test-token-1"
    },
    body: JSON.stringify(data)
  })
  .then(res => {
    if (!res.ok) throw new Error("API error");
    return res.json();
  })
  .then(() => alert("Rule submitted successfully!"))
  .catch(err => alert("Error: " + err.message));
}

// Pre-fill example
window.onload = () => {
  addSimpleCondition(undefined, "SPEL", "T(java.util.List).of('UID1','UID2').contains(#target.callContext?.uid)");
  addAction("SPEL", "#target.candidateCDNs = #target.candidateCDNs.?[id == 'some-cdn.domain']");
  updateJsonPreview();
};