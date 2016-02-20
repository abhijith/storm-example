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
  def process(tup)
    log tup.attributes
    begin
      vals = tup.values
      if not vals.empty?
        partition_key, sequence_number, record_data = vals
        emit([record_data])
      end
    rescue Exception => e
      emit([e.message])
    end
  end
end

SimpleBolt.new.run
