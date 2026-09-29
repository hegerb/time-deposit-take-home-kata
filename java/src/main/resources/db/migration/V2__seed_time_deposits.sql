-- A8: sample deposits around every interest boundary so the endpoints show each rule at work.

INSERT INTO "timeDeposits" ("id", "planType", "days", "balance") VALUES
    (1, 'basic',   20,  1000.00),
    (2, 'basic',   31,  1000.00),
    (3, 'student', 200, 2500.00),
    (4, 'student', 366, 2500.00),
    (5, 'premium', 45,  5000.00),
    (6, 'premium', 46,  5000.00);

INSERT INTO "withdrawals" ("id", "timeDepositId", "amount", "date") VALUES
    (1, 2, 100.00, '2024-04-15'),
    (2, 3, 250.00, '2024-06-01'),
    (3, 3,  50.00, '2024-06-20');
