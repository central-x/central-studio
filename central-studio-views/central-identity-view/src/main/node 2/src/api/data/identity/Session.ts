import type { Account } from '@/api/data/organization/Account';

export interface Session {
  id?: string;
  sessionId?: string; // 会话标识符
  accountId?: string;
  account?: Account;
  clientIp?: string;
  clientAddress?: string;
  clientDevice?: string;
  clientBrowser?: string;
  clientOs?: string;
  lastAccessTime?: number;
  createTime?: number;
  loginTime?: number; // 登录时间
  location?: string; // IP归属地
  active?: boolean;
}

export interface SessionInfo {
  // 会话ID
  id: string;
  // 登录时间
  loginTime: string;
  // 最后访问时间
  lastAccessTime: string;
  // IP地址
  ip: string;
  // IP归属地
  location: string;
  // 设备类型 (desktop, mobile, tablet)
  deviceType: 'desktop' | 'mobile' | 'tablet';
  // 浏览器和操作系统
  client: string;
  // 是否为当前会话
  isCurrentSession: boolean;
} 