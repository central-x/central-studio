import type { Account } from '@/api/data/organization/Account';
import type { Department } from '@/api/data/organization/Department';
import type { Unit } from '@/api/data/organization/Unit';
import type { Rank } from '@/api/data/organization/Rank';

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
