import http from '../lib/axios';
import type { User } from '../types';

export class UserService {
  static async getUser(uid: string): Promise<User> {
    return (await http.get(`/users/${uid}`)) as unknown as User;
  }

  static async upsertUser(uid: string, email: string): Promise<void> {
    await http.post('/users', { uid, email });
  }
}
