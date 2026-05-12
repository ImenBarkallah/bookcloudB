INSERT INTO library_branches (name, address, opening_hours)
SELECT 'Main Branch', 'Default branch', 'Mon-Fri 09:00-17:00'
WHERE NOT EXISTS (SELECT 1 FROM library_branches);

INSERT INTO library_settings (
    default_loan_days,
    max_active_loans_default,
    reservation_expiry_days,
    fine_per_day,
    max_renewals_per_loan,
    overdue_fine_per_day_cents
)
SELECT 14, 5, 7, 0, 2, 0
WHERE NOT EXISTS (SELECT 1 FROM library_settings);
