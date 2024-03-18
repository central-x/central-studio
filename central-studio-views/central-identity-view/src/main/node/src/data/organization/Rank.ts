import type { Unit } from '@/data/organization/Unit'
import type { Account } from '@/data/organization/Account'

export interface Rank {
  id?: string;
  code?: string;
  name?: string;
  unitId?: string;
  unit?: Unit;
  order?: number;

  creatorId?: string;
  createDate?: number;
  creator?: Account;
  modifierId?: string;
  modifiedDate?: number;
  modifier?: Account;
}