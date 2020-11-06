require_relative "../storm"

class DebugSpout < Storm::Spout

  def nextTuple
    s = ["a little brown dog",
         "the man petted the dog",
         "four score and seven years ago",
         "an apple a day keeps the doctor away"]
    sentence = s.sample
    sleep(5)
    emit([sentence])
  end

end

DebugSpout.new.run
