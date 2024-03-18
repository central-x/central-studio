import type { Account } from '@/data/organization/Account'
import type { Department } from '@/data/organization/Department'
import type { Unit } from '@/data/organization/Unit'
import type { Rank } from '@/data/organization/Rank'

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