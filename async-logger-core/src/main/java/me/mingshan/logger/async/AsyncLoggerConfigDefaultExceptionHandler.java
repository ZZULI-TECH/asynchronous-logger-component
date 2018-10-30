/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.mingshan.logger.async;

import com.lmax.disruptor.ExceptionHandler;

/**
 * 异步日志异常处理
 *
 * @author mingshan
 */
public class AsyncLoggerConfigDefaultExceptionHandler implements ExceptionHandler<RingBufferLogEvent> {

    @Override
    public void handleEventException(final Throwable throwable, final long sequence, final RingBufferLogEvent event) {
        try {
            // Careful to avoid allocation in case of memory pressure.
            // Sacrifice performance for safety by writing directly
            // rather than using a buffer.
            System.err.print("AsyncLogger error handling event seq=");
            System.err.print(sequence);
            System.err.print(", value='");
            try {
                System.err.print(event);
            } catch (Throwable t) {
                System.err.print("ERROR calling toString() on ");
                System.err.print(event.getClass().getName());
                System.err.print(": ");
                System.err.print(t.getClass().getName());
                System.err.print(": ");
                System.err.print(t.getMessage());
            }
            System.err.print("': ");
            System.err.print(throwable.getClass().getName());
            System.err.print(": ");
            System.err.println(throwable.getMessage());
            // Attempt to print the full stack trace, which may fail if we're already
            // OOMing We've already provided sufficient information at this point.
            throwable.printStackTrace();
        } catch (Throwable ignored) {
            // Throwing an error here may kill the background thread.
        }
    }

    @Override
    public void handleOnStartException(final Throwable throwable) {
        System.err.println("AsyncLogger error starting:");
        throwable.printStackTrace();
    }

    @Override
    public void handleOnShutdownException(final Throwable throwable) {
        System.err.println("AsyncLogger error shutting down:");
        throwable.printStackTrace();
    }
}
