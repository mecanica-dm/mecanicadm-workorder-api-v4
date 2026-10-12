CREATE TABLE budget_decision_tokens (
    id UUID NOT NULL PRIMARY KEY,
    work_order_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_budget_decision_tokens_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders(id)
);

CREATE UNIQUE INDEX idx_budget_decision_tokens_token ON budget_decision_tokens(token);
CREATE INDEX idx_budget_decision_tokens_work_order_id ON budget_decision_tokens(work_order_id);
