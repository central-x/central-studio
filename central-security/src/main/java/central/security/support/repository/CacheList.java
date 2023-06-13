/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.security.support.repository;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

/**
 * 缓存列表
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public interface CacheList {
    /**
     * 获取指定区间的元素
     *
     * @param start 开始下标（包含）
     * @param end   结束下标（不包含）
     * @return [start, end)
     */
    List<String> range(long start, long end);

    /**
     * 删除指定区间的元素
     *
     * @param start 开始下标（包含）
     * @param end   结束下标（不包含）
     */
    void trim(long start, long end);

    /**
     * 获取列表元素数量
     */
    long size();

    /**
     * 在列表最前端插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long leftPush(String... values);

    /**
     * 在列表最前端插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long leftPush(Collection<String> values);

    /**
     * 如果列表存在，则在该列表最前方插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long leftPushIfPresent(String... values);

    /**
     * 如果列表存在，则在该列表最前方插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long leftPushIfPresent(Collection<String> values);

    /**
     * 在列表最后端插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long rightPush(String... values);

    /**
     * 在列表最后端插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long rightPush(Collection<String> values);

    /**
     * 如果列表存在，在列表最后端插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long rightPushIfPresent(String... values);

    /**
     * 如果列表存在，在列表最后端插入元素
     *
     * @param values 待插入元素
     * @return 插入后列表大小
     */
    long rightPushIfPresent(Collection<String> values);

    /**
     * 将指定下标的元素替换成指定的值
     *
     * @param index 下标
     * @param value 值
     */
    void set(long index, String value);

    /**
     * 移除指定元素
     *
     * @param count 移除前几个出现的元素。如果 count > 0，则从头向尾移除指定元素数量；count < 0，则从尾向头移除指定元素数量；如果 count = 0，则移除所有元素
     * @param value 待移除的元素
     * @return 已移除的元素数量
     */
    long remove(long count, String value);

    /**
     * 移除指定下标的元素
     *
     * @param index 待移除的下标
     */
    long remove(long index);

    /**
     * 从头向尾检索，返回元素第一次出现的下标
     *
     * @param value 待定位的元素
     * @return 元素所处下标。返回 -1 时表示元素不存在
     */
    long indexOf(String value);

    /**
     * 从尾向头检索，返回元素第一次出现的下标
     *
     * @param value 待定位的元素
     * @return 元素所处下标。返回 -1 时表示元素不存在
     */
    long lastIndexOf(String value);

    /**
     * 从头向尾，移除并返回第一个元素
     *
     * @return 被移除的第一个元素。如果列表为空，则返回空
     */
    String leftPop();

    /**
     * 从头向尾，移除并返回前几个元素
     *
     * @param count 指定个数
     * @return 被移除的前几个元素
     */
    List<String> leftPop(long count);

    /**
     * 从头向尾，移除并返回第一个元素
     * <p>
     * 这是一个阻塞方法，该方法会阻塞进程直到超时或列表中有元素返回
     *
     * @param timeout 超时时间
     * @return 被移除的第一个元素。如果列表为空，则返回空
     */
    String leftPop(Duration timeout);

    /**
     * 从尾向头，移除并返回第一个元素
     *
     * @return 被移除的第一个元素。如果列表为空，则返回空
     */
    String rightPop();

    /**
     * 从尾向头，移除并返回前几个元素
     *
     * @param count 指定个数
     * @return 被移除的前几个元素
     */
    List<String> rightPop(long count);

    /**
     * 从尾向头，移除并返回第一个元素
     * <p>
     * 这是一个阻塞方法，该方法会阻塞进程直到超时或列表中有元素返回
     *
     * @param timeout 超时时间
     * @return 被移除的第一个元素。如果列表为空，则返回空
     */
    String rightPop(Duration timeout);
}
