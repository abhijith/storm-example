require "./storm"

class SplitSentenceBolt < Storm::Bolt
  def process(tup)
    begin
      a = tup.attributes.to_s
      File.open("/home/vagrant/tuples.log", "a+") {|f| f.write(a + "\n") }
      vals = tup.values[0]
      if vals.nil?
        emit(["nil"])
      else
        vals.split(" ").each do |word|
          emit([word])
        end
      end
    rescue Exception => e
      emit([e.message])
    end
  end
end

SplitSentenceBolt.new.run
