# TEMPORARY TEST SERVER

import asyncio
import message_pb2

async def handle_dummy(reader, writer):
    print("Received client")
    protobuf = message_pb2.Message()
    protobuf.key = "light"
    writer.write(protobuf.SerializeToString())
    await writer.drain()
    print("Wrote protobuf message")
    writer.close()

async def main():
    server = await asyncio.start_server(
        handle_dummy, '127.0.0.1', 6969
    )

    async with server:
        await server.serve_forever()

asyncio.run(main())
