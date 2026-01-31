export interface Account {
  name: string;
  slug: string;
  description: string;
}

export interface AccountDetailResult {
  account: Account;
  // Add other properties as needed (e.g., assets, passives)
}


