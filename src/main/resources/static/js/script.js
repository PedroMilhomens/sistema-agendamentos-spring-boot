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
                    <td>${agendamento.data}</td>
                    <td>R$ ${agendamento.hora}</td>
                    <td>${agendamento.descricao}</td>
                `;

                // Adiciona a linha dentro do corpo da tabela
                tbody.appendChild(linha);
            });
        })
        .catch(erro => console.error('Erro ao buscar agendamentos:', erro));
}

// Configura o botão para chamar a função quando clicado
document.getElementById('btnAtualizar').addEventListener('click', carregarTabela);