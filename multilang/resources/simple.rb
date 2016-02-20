require "./storm"

# {
#   :id => "1442223452700172152",
#   :component => "echo-bolt",
#   :stream => "default",
#   :task => 3,
#   :values => [
#               "123",
#               "49559350852826080610835232965216934972905563651422814210",
#               {
#                 ":partition-key" => "123",
#                 ":sequence-number" => "49559350852826080610835232965216934972905563651422814210",
#                 ":data" => "testdata-400"
#               }
#              ]
# }

class SimpleBolt < Storm::Bolt
  @acc   = []
  @count = 0
  class << self
    attr_accessor :acc, :count
  end

  def process(tup)
    attrs = tup.attributes
    logInfo attrs
    logInfo SimpleBolt.acc
    logInfo SimpleBolt.count

    begin
      vals = tup.values
      if not vals.empty?
        if tup.tick?
          emit(SimpleBolt.acc)
          ack(tup)
          SimpleBolt.acc = []
        else
          partition_key, sequence_number, record_data = vals
          SimpleBolt.acc << record_data
        end
      end
    rescue StandardError => e
      emit([e.message])
    end
  end
end

SimpleBolt.new.run
