package com.mdsft.particles.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.mdsft.particles.Constants.*;
import static android.opengl.GLES20.*;

public class VertexBuffer {
    private final int bufferId;

    public VertexBuffer(float[] vertexData) {
        // Allocate a buffer.
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }
        bufferId = buffers[0];

        // Bind to the buffer.
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);

        // Transfer data to native memory.
        FloatBuffer vertexArray = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                                            .order(ByteOrder.nativeOrder())
                                            .asFloatBuffer()
                                            .put(vertexData);
        vertexArray.position(0);

        // Transfer data from native memory to the GPU buffer.
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PER_FLOAT,
            vertexArray, GL_STATIC_DRAW);

        // IMPORTANT: Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount,
                                       int stride) {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false,
            stride, dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
