## Planeamento do Projeto

### Milestone 2 (Semana 6 - 12/10/2026)
**Objetivo:** Criação da base da aplicação com UI reativa (dados em memória) e estrutura inicial.
* Criação da aplicação com uma UI reativa incluindo os 5 ecrãs principais: Title, Settings, About, Chat History e Active Chat.
* Implementação da navegação entre ecrãs e regra de arranque (redirecionamento automático para o Active Chat caso houvesse conversa ativa no encerramento)[cite: 7].
* Definição do modelo de domínio de conversa (estrutura, mensagens e roles: USER / MODEL) e criação dos ViewModels de cada ecrã.
* Definição da estratégia de verificação, primeiros testes de domínio e gravação do vídeo de demonstração e discussão de decisões[cite: 7].

### Milestone 3 (Semana 11 - 16/11/2026)
**Objetivo:** Integração com o modelo Gemini e estado persistente.
* Implementação da classe Application como Service Locator (sem Hilt/Dagger)[cite: 7].
* Definição de DTOs e configuração do cliente Ktor (Kotlinx Serialization) para o endpoint generateContent[cite: 7].
* Implementação da lógica stateless: construção de payload com histórico completo, alternância de papéis e configuração do system_instruction para a persona de mentor Android[cite: 7].
* Persistência de estado com Room (guardar/ler conversas) e DataStore (armazenamento da chave BYOK)[cite: 7].
* Verificação de conectividade antes de cada chamada, tratamento de erros HTTP e atualização dos ViewModels (loading, erro, offline)[cite: 7].
* Gravação do vídeo de demonstração das funcionalidades implementadas[cite: 7].

### Milestone Final (Prazo 12/12/2026)
**Objetivo:** Funcionalidades avançadas, estabilização e entrega final.
* Adição das funcionalidades de reescrever mensagens e de remover conversas do histórico[cite: 7].
* (Opcional) Capacidade de adicionar imagens da galeria ou câmara às conversas, persistindo apenas o URI no Room[cite: 7].
* Finalização da UI, polimento e todos os testes de domínio necessários.
* Gravação do vídeo final demonstrando o funcionamento integral do projeto e decisões tomadas[cite: 7].

---

## Cronograma Semanal (Timeline)

* **21/09 - 27/09:** Criação da aplicação, definição da estrutura de packages e aplicar um tema base. Definição do modelo de domínio da conversa.
* **28/09 - 04/10:** Criação dos ecrãs (About, Settings, Chat History, Active Chat, Title) juntamente com a navegação entre eles. Criação da regra de arranque.
* **05/10 - 11/10:** Criação do vídeo e dos testes de domínio.
* **12/10 - 18/10:** Mudança dos ViewModels.
* **19/10 - 25/10:** Criação da classe Application como Service Locator.
* **26/10 - 01/11:** Definição das DTOs, configuração do cliente Ktor, configuração do system_instruction, verificação e tratamento de erros HTTP.
* **02/11 - 08/11:** Criação da Persistência (Room e DataStore). Mudanças da UI para refletir os estados reais.
* **09/11 - 15/11:** Criação do vídeo e de testes de domínio adicionais.
* **16/11 - 12/12:** Reescrever e remover conversas. Capacidade de adicionar imagens às conversas. Criação dos testes de domínio finais e do vídeo final.

---

## Divisão de Tarefas

* **Tomás Duarte:** UI/Compose, ecrãs, tema e navegação.
* **Guilherme Nobre:** Dados e rede: Room, DataStore, Ktor cliente Gemini, lógica stateless.
* **Valentim Sukhar:** Domínio, ViewModels, offline/erros, verificação e testes.


* Todas as informações neste documento inclusive a atribuição indivídual da carga de trabalho é sujeita a mudança conforme o necessário.
