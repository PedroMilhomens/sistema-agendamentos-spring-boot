carregarTabela();

// GET METHOD
function carregarTabela() {
    fetch("api/agendamentos")
        .then(response => response.json())
        .then(agendamentos => {
            const tbody = document.getElementById('corpo');

            // Limpa a tabela
            tbody.innerHTML = "";

            // Percorre a lista de agendamentos vinda do Spring Boot
            agendamentos.forEach(agendamento => {
                // Cria uma nova linha (tr)
                const linha = document.createElement('tr');

                // Preenche os dados da linha com as colunas (td)
                linha.innerHTML = `
                    <td>${agendamento.id}</td>
                    <td>${agendamento.nome}</td>
                    <td>${agendamento.data}</td>
                    <td>${agendamento.hora}</td>
                    <td>${agendamento.descricao}</td>
                    <td><button id="edit" onclick="showModal(
                        'editDialog', 
                        ${agendamento.id}, 
                        'editTransferId')">✏️</button></td>
                    <td><button id="delete" onclick="showModal(
                        'deleteDialog', 
                        ${agendamento.id}, 
                        'delTransferId')">🗑️</button></td>
                `;

                // Adiciona a linha dentro do corpo da tabela
                tbody.appendChild(linha);
            });
        })
        .catch(erro => console.error('Erro ao buscar agendamentos:', erro));
}

// POST METHOD
document.getElementById('formCadastro').addEventListener('submit', salvarAgendamentos);
function salvarAgendamentos(event) {
    event.preventDefault();

    const newAgendamento = {
        nome: document.getElementById("nome").value,
        data: document.getElementById("data").value,
        hora: document.getElementById("hora").value,
        descricao: document.getElementById("descricao").value,
        doctorId: document.getElementById("doctorId").value
    }
    // 2. Faz o fetch configurado para POST
    fetch('/api/agendamentos/post', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json' // Avisa o Spring Boot que estamos enviando um JSON
        },
        body: JSON.stringify(newAgendamento) // Converte o objeto JS em texto JSON
    })
        .then(response => {
            if (response.ok) {
                alert('Agendamento salvo com sucesso!');
                carregarTabela(); // Atualiza a tabela na tela
            }
        })
        .catch(erro => console.error('Erro no POST:', erro));
}

// PUT METHOF
/*document.getElementById('edit').addEventListener('click', editarAgendamento);
function editarAgendamento(id, event) {
}
*/

// DELETE METHOD
document.getElementById('confirmDelete').addEventListener('click', excluirAgendamento);
function excluirAgendamento(event) {
    event.preventDefault();

    const id = Number(document.getElementById("delTransferId").innerHTML);

    fetch(`/api/agendamentos/${id}`, {method: 'DELETE'})
        .then(response => {
            if (response.ok) {
                carregarTabela()
            }
        })
        .catch(erro => console.error('Erro no DELETE:', erro));

    document.getElementById('deleteDialog').close();
}

function showModal(dialog, id, elementID) {
    const transferId = document.getElementById(elementID);

    document.getElementById(dialog).showModal();
    transferId.innerHTML = id;


}

