import type { Currency, TransactionCategory } from "./types/Transaction";

/** Must match backend `FazDefaults.BOOK_CURRENCY`. */
export const DOMAIN_BOOK_CURRENCY = "USD" as const satisfies Currency;

/** Empty-form combobox default — UI convenience; domain still requires category on submit. */
export const UI_DEFAULT_CATEGORY = "OTHER" as const satisfies TransactionCategory;

/** Initial currency in the form — aligned to domain book currency. */
export const UI_DEFAULT_CURRENCY = DOMAIN_BOOK_CURRENCY;
