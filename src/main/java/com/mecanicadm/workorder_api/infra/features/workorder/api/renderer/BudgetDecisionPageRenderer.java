package com.mecanicadm.workorder_api.infra.features.workorder.api.renderer;

public final class BudgetDecisionPageRenderer {

    private static final String BASE_STYLE = """
            * { box-sizing: border-box; margin: 0; padding: 0; }
            body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f2f5; display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; }
            .container { background: white; border-radius: 16px; box-shadow: 0 4px 24px rgba(0,0,0,0.08); padding: 48px 40px; max-width: 500px; width: 100%%; }
            h1 { font-size: 1.4em; margin-bottom: 24px; text-align: center; }
            label { display: block; color: #555; font-size: 0.95em; font-weight: 600; }
            textarea { width: 100%%; min-height: 150px; padding: 14px; border: 1px solid #ddd; border-radius: 10px; font-size: 1em; font-family: inherit; resize: vertical; transition: border-color 0.2s; }
            textarea:focus { outline: none; border-color: #4caf50; box-shadow: 0 0 0 3px rgba(76,175,80,0.15); }
            .buttons { display: flex; gap: 12px; }
            .btn { flex: 1; padding: 14px; border: none; border-radius: 10px; font-size: 1em; font-weight: 600; cursor: pointer; text-decoration: none; text-align: center; transition: background 0.2s; }
            .btn-submit { background: #4caf50; color: white; }
            .btn-submit:hover { background: #43a047; }
            .btn-cancel { background: #f5f5f5; color: #666; }
            .btn-cancel:hover { background: #eee; }
            .icon { font-size: 3.5em; margin-bottom: 20px; }
            .message { color: #555; font-size: 1.05em; line-height: 1.7; }
            .footer { margin-top: 36px; padding-top: 16px; border-top: 1px solid #eee; text-align: center; }
            .footer p { color: #aaa; font-size: 0.8em; }
            .text-center { text-align: center; }
            form { width: 100%%; display: flex; flex-direction: column; gap: 4px; }""";

    private static final String FOOTER = """
            <div class="footer">
                <p>MecânicaDM - Sistema de Gestão de Oficina Mecânica</p>
            </div>""";

    private BudgetDecisionPageRenderer() {
    }

    public static String formPage(String title, String token, String action, String label, boolean required) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        %s
                        h1 { color: #333; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>%s</h1>
                        <form method="POST" action="/budget-decision/%s">
                            <input type="hidden" name="action" value="%s">
                            <label for="observation">%s</label>
                            <textarea id="observation" name="observation" placeholder="Digite aqui..." %s></textarea>
                            <div class="buttons">
                                <a href="#" onclick="history.back()" class="btn btn-cancel">Cancelar</a>
                                <button type="submit" class="btn btn-submit">Enviar</button>
                            </div>
                        </form>
                        %s
                    </div>
                </body>
                </html>
                """.formatted(title, BASE_STYLE, title, token, action, label, required ? "required" : "", FOOTER);
    }

    public static String successPage(String action) {
        String message = switch (action) {
            case "APPROVED" -> "Orçamento aprovado com sucesso!";
            case "REJECTED" -> "Orçamento rejeitado. Obrigado pela resposta.";
            case "CHANGES_REQUESTED" -> "Solicitação de alterações enviada com sucesso!";
            default -> "Resposta registrada com sucesso!";
        };

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Resposta Registrada</title>
                    <style>
                        %s
                        h1 { color: #2e7d32; }
                        .icon { color: #2e7d32; }
                    </style>
                </head>
                <body>
                    <div class="container text-center">
                        <div class="icon">&#10003;</div>
                        <h1>%s</h1>
                        <p class="message">Obrigado por responder ao orçamento.</p>
                        %s
                    </div>
                </body>
                </html>
                """.formatted(BASE_STYLE, message, FOOTER);
    }

    public static String errorPage(String title, String message) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        %s
                        h1 { color: #c62828; }
                        .icon { color: #c62828; }
                    </style>
                </head>
                <body>
                    <div class="container text-center">
                        <div class="icon">&#9888;</div>
                        <h1>%s</h1>
                        <p class="message">%s</p>
                        %s
                    </div>
                </body>
                </html>
                """.formatted(title, BASE_STYLE, title, message, FOOTER);
    }
}
