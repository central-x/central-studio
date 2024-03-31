import type { Account } from "./Account";
import type { Department } from "./Department";
import type { Unit } from "./Unit";
import type { Rank } from "./Rank";

export interface AccountUnit {
  id?: string;
  accountId?: string;
  account?: Account;
  unitId?: string;
  unit?: Unit;
  departments?: Department[];
  rankId?: string;
  rank?: Rank;
  primary?: boolean;

  creatorId?: string;
  createDate?: number;
  creator?: Account;
  modifierId?: string;
  modifiedDate?: number;
  modifier?: Account;
}
