package ru.practicum.kafka.serializer;

//@Slf4j
//public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {
//    private final EncoderFactory encoderFactory = EncoderFactory.get();
//    private BinaryEncoder encoder;
//
//
//    @Override
//    public byte[] serialize(String topic, SpecificRecordBase data) {
//        if (data == null) {
//            log.info("сериализация данных не удачно topic: " + topic);
//            return null;
//        }
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            byte[] result = null;
//            encoder = encoderFactory.binaryEncoder(outputStream, encoder);
//
//            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());
//            writer.write(data, encoder);
//            encoder.flush();
//
//            result = outputStream.toByteArray();
//            log.info("сериализация данных topic: " + topic);
//            return result;
//        } catch (IOException e) {
//            throw new SerializationException("Ошибка сериализации данных topic: " + topic);
//        }
//    }
//}
